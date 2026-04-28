package com.ian.rinha_backend.service;

import com.ian.rinha_backend.dto.FraudResponse;
import com.ian.rinha_backend.dto.ReferenceScore;
import com.ian.rinha_backend.dto.TransactionPayload;
import com.ian.rinha_backend.model.ReferencesModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class VectorSearch {

    public static final double MAX_AMOUNT = 10000.0;
    public static final double MAX_INSTALLMENTS = 12.0;
    public static final double AMOUNT_VS_AVG_RATIO = 10.0;
    public static final double MAX_MINUTES = 1440.0;
    public static final double MAX_KM = 1000.0;
    public static final int MAX_TX_COUNT_24H = 20;
    public static final double MAX_MERCHANT_AVG_AMOUNT = 10000.0;
    public static final double THRESHOLD = 0.6;


    private final Map<String, Double> MCC_RISK_SCORE;
    private final ReferencesModel[] REFERENCES;

    public VectorSearch(@Qualifier("mccRiskMap") Map<String, Double> MCC_RISK_SCORE, @Qualifier("references") ReferencesModel[] references) {
        this.MCC_RISK_SCORE = MCC_RISK_SCORE;
        REFERENCES = references;
    }

    private double[] createVector(TransactionPayload payload) {
        double[] vector = new double[15];
        vector[0] = limit(payload.transaction().amount() / MAX_AMOUNT);
        vector[1] = limit(payload.transaction().installments() / MAX_INSTALLMENTS);
        vector[2] = limit(payload.transaction().amount()/ payload.customer().avg_amount() / AMOUNT_VS_AVG_RATIO);
        int hour = payload.transaction().requested_at().atZone(ZoneId.systemDefault()).getHour();
        int day = payload.transaction().requested_at().atZone(ZoneId.systemDefault()).getDayOfWeek().getValue();
        vector[3] = limit((double) hour / 23);
        vector[4] = limit((double) day / 7);
        vector[5] = (payload.last_transaction() != null) ? limit(Duration.between(payload.last_transaction().timestamp().atZone(ZoneId.systemDefault()).toLocalTime(), LocalTime.now()).toMinutes() / MAX_MINUTES) : -1;
        vector[6] = (payload.last_transaction() != null) ? limit(payload.last_transaction().km_from_current() / MAX_KM) : -1;
        vector[7] = limit(payload.terminal().km_from_home() / MAX_KM);
        vector[8] = limit((double) (payload.customer().tx_count_24h() / MAX_TX_COUNT_24H));
        vector[9] = (payload.terminal().is_online()) ? 1 : 0;
        vector[10] = (payload.terminal().card_present()) ? 1 : 0;
        vector[11] = (payload.customer().known_merchants().contains(payload.merchant().id())) ? 0 : 1;

        vector[12] = MCC_RISK_SCORE.getOrDefault(payload.merchant().mcc(), 0.5);
        vector[13] = limit(payload.merchant().avg_amount()/ MAX_MERCHANT_AVG_AMOUNT);

        return vector;
    }

    public FraudResponse vectorSearch(TransactionPayload payload) {
        double[] vector = createVector(payload);
        List<ReferenceScore> list = new ArrayList<>();
        for (ReferencesModel reference : REFERENCES) {
            double res = 0;
            double d0 = (vector[0] - reference.getVector().getFirst());
            res += d0 * d0;
            double d1 = (vector[1] - reference.getVector().get(1));
            res += d1 * d1;
            double d2 = (vector[2] - reference.getVector().get(2));
            res += d2 * d2;
            double d3 = (vector[3] - reference.getVector().get(3));
            res += d3 * d3;
            double d4 = (vector[4] - reference.getVector().get(4));
            res += d4 * d4;
            double d5 = (vector[5] - reference.getVector().get(5));
            res += d5 * d5;
            double d6 = (vector[6] - reference.getVector().get(6));
            res += d6 * d6;
            double d7 = (vector[7] - reference.getVector().get(7));
            res += d7 * d7;
            double d8 = (vector[8] - reference.getVector().get(8));
            res += d8 * d8;
            double d9 = (vector[9] - reference.getVector().get(9));
            res += d9 * d9;
            double d10 = (vector[10] - reference.getVector().get(10));
            res += d10 * d10;
            double d11 = (vector[11] - reference.getVector().get(11));
            res += d11 * d11;
            double d12 = (vector[12] - reference.getVector().get(12));
            res += d12 * d12;
            double d13 = (vector[13] - reference.getVector().getLast());
            res += d13 * d13;

            res = Math.sqrt(res);

            list.add(new ReferenceScore(reference.getLabel(), res));
        }
        list.sort(Comparator.comparing(ReferenceScore::score));
        int fraud = 0;
        for (int i = 5; i >= 0; i--) {
            if (list.get(i).label().equals("fraud")) {
                fraud++;
            }
        }

        int score = fraud / 5;
        boolean approved = score < THRESHOLD;

        return (approved) ? new FraudResponse(true, score) : new FraudResponse(false, score);
    }

    private double limit(double n) {
        if(n > 1) return 1;
        if(n < 0) return 0;

        return n;
    }
}

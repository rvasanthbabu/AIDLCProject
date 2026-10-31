package com.ideationportal.evaluation.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ideationportal.evaluation.api.dto.EvaluationResponse;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Round-trip property for EvaluationResponse DTO (PBT-02) with jqwik.
 */
class EvaluationResponseRoundTripPropertyTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Property
    void jsonRoundTripIsIdentity(@ForAll("responses") EvaluationResponse original) throws Exception {
        String json = mapper.writeValueAsString(original);
        EvaluationResponse restored = mapper.readValue(json, EvaluationResponse.class);
        assertThat(restored).isEqualTo(original);
    }

    @Provide
    Arbitrary<EvaluationResponse> responses() {
        Arbitrary<UUID> ids = Arbitraries.create(UUID::randomUUID);
        Arbitrary<Integer> metric = Arbitraries.integers().between(1, 5);
        Arbitrary<BigDecimal> score = Arbitraries.integers().between(0, 10000)
                .map(i -> BigDecimal.valueOf(i, 2)); // 0.00 .. 100.00
        Arbitrary<String> comment = Arbitraries.strings().ofMaxLength(200);
        Arbitrary<Instant> instants = Arbitraries.longs().between(0L, 4_000_000_000_000L).map(Instant::ofEpochMilli);

        Arbitrary<Object[]> head = Combinators.combine(ids, ids, ids, metric, metric, metric)
                .as((id, idea, member, f, i, n) -> new Object[]{id, idea, member, f, i, n});

        return Combinators.combine(head, score, comment, instants).as((h, sc, c, ts) ->
                new EvaluationResponse(
                        (UUID) h[0], (UUID) h[1], (UUID) h[2],
                        (int) h[3], (int) h[4], (int) h[5],
                        sc, c, ts));
    }
}

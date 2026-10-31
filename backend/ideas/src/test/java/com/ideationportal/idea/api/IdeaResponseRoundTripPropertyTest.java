package com.ideationportal.idea.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ideationportal.idea.api.dto.IdeaResponse;
import com.ideationportal.idea.domain.IdeaCategory;
import com.ideationportal.idea.domain.IdeaState;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based round-trip test for the IdeaResponse DTO (PBT-02).
 * serialize -> deserialize must equal the original. jqwik provides generation,
 * shrinking, and seed-based reproducibility (PBT-07/08).
 */
class IdeaResponseRoundTripPropertyTest {

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Property
    void jsonRoundTripIsIdentity(@ForAll("ideaResponses") IdeaResponse original) throws Exception {
        String json = mapper.writeValueAsString(original);
        IdeaResponse restored = mapper.readValue(json, IdeaResponse.class);
        assertThat(restored).isEqualTo(original);
    }

    @Provide
    Arbitrary<IdeaResponse> ideaResponses() {
        Arbitrary<UUID> ids = Arbitraries.create(UUID::randomUUID);
        Arbitrary<String> text = Arbitraries.strings().ofMaxLength(200);
        Arbitrary<IdeaCategory> categories = Arbitraries.of(IdeaCategory.class);
        Arbitrary<IdeaState> states = Arbitraries.of(IdeaState.class);
        // Truncate to milliseconds so JSON round-trip is lossless at the serialized precision.
        Arbitrary<Instant> instants = Arbitraries.longs()
                .between(0L, 4_000_000_000_000L)
                .map(Instant::ofEpochMilli);

        // Combine the first eight generators, then nest the remaining two.
        Arbitrary<Object[]> head = Combinators.combine(ids, ids, ids, text, text, text, text, categories)
                .as((id, submitter, cycle, title, problem, solution, impact, cat) ->
                        new Object[]{id, submitter, cycle, title, problem, solution, impact, cat});

        return Combinators.combine(head, states, instants).as((h, state, instant) ->
                new IdeaResponse(
                        (UUID) h[0], (UUID) h[1], (UUID) h[2],
                        (String) h[3], (String) h[4], (String) h[5], (String) h[6],
                        (IdeaCategory) h[7], state, instant));
    }
}

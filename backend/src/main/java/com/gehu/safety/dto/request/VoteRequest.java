package com.gehu.safety.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class VoteRequest {

    /**
     * +1 to upvote, -1 to downvote, 0 to remove an existing vote.
     * Sending the same value twice in a row (e.g. +1 then +1 again) also
     * removes the vote - handled as a toggle in the service layer.
     */
    @NotNull(message = "voteValue is required")
    @Min(value = -1, message = "voteValue must be -1, 0 or 1")
    @Max(value = 1, message = "voteValue must be -1, 0 or 1")
    private Integer voteValue;
}

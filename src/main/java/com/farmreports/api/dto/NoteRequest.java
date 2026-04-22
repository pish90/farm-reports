package com.farmreports.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record NoteRequest(@NotNull List<NoteEntry> notes) {
    public record NoteEntry(
            @NotNull Integer subjectId,
            String subjectKey,
            String note
    ) {}
}

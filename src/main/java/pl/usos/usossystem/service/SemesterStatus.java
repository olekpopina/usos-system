package pl.usos.usossystem.service;

public enum SemesterStatus {
    W_TRAKCIE("W trakcie"),
    ZALICZONY("Zaliczony"),
    WARUNKOWY("Warunkowy"),
    NIEZALICZONY("Niezaliczony");

    private final String displayName;

    SemesterStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canRegisterNextSemester() {
        return this == ZALICZONY || this == WARUNKOWY;
    }

    public static SemesterStatus fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            return W_TRAKCIE;
        }

        for (SemesterStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(value.trim())) {
                return status;
            }
        }

        return W_TRAKCIE;
    }
}

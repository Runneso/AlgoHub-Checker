package ru.runneso.checker.domain.entities;

import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity {
    protected final UUID oid;

    protected BaseEntity() {
        this.oid = UUID.randomUUID();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof BaseEntity that)) return false;
        return Objects.equals(oid, that.oid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oid);
    }
}

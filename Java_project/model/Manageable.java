package model;

/**
 * Defines common lifecycle actions for records that can be managed by the system.
 */
public interface Manageable {
    void activate();

    void suspend();

    void graduate();

    boolean isActive();
}

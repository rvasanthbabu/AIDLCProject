package com.ideationportal.identity.domain;

/**
 * Single role per user (decision Q1=A). Idea submission requires SUBMITTER (BR-U1-11).
 */
public enum Role {
    SUBMITTER,
    PANEL_MEMBER,
    ADMINISTRATOR,
    VIEWER
}

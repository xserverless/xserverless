package io.xserverless.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tb_event")
@Data
public class EventDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type")
    private String type;
    @Column(name = "timestamp")
    private Long timestamp;
    @Column(name = "function_id")
    private Long functionId;
    @Column(name = "env_domain")
    private String envDomain;
    @Column(name = "server")
    private String server;
}

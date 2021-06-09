package io.xserverless.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tb_function_task")
@Data
public class FunctionTaskDomain {
    public static final int INIT = 0;
    public static final int STARTED = 1;
    public static final int FINISHED = 2;

    public static final long TIMEOUT = 10L * 60 * 1000 * 1000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    // storage
    @Column(name = "path")
    private String path;

    // status
    @Column(name = "status")
    private Integer status;

    @Column(name = "start_timestamp")
    private Long startTimestamp;

    @Column(name = "finish_timestamp")
    private Long finishTimestamp;
}

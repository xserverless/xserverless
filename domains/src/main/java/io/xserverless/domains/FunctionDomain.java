package io.xserverless.domains;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "tb_function")
@Data
public class FunctionDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task_id")
    private Long taskId;

    // storage
    @Column(name = "path")
    private String path;

    // entry
    @Column(name = "owner")
    private String owner;
    @Column(name = "name")
    private String name;
    @Column(name = "descriptor")
    private String descriptor;

    // url pattern
    @Column(name = "url_pattern")
    private String urlPattern;
    @Column(name = "http_methods")
    private Integer httpMethods; //-- 0x0001: get, 0x0010: post, 0x0100: put, 0x1000: delete

    public static final int HTTP_METHOD_GET = 0x00001;
    public static final int HTTP_METHOD_POST = 0x00010;
    public static final int HTTP_METHOD_PUT = 0x00100;
    public static final int HTTP_METHOD_DELETE = 0x01000;
}

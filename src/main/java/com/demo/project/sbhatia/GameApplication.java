package com.demo.project.sbhatia;

import com.demo.project.sbhatia.service.impl.StorageServiceImpl;
import io.vertx.reactivex.core.Vertx;

public class GameApplication {

  private final static int DEFAULT_PORT = 8080;

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    new Server(vertx, new StorageServiceImpl(), DEFAULT_PORT).start();
  }
}

package com.demo.project.sbhatia;

import com.demo.project.sbhatia.service.StorageService;
import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

  public final static String NICK_NAME = "nick_name";
  public final static String POINTS = "points";
  public final static String TO = "to";

  private static final Logger log = LoggerFactory.getLogger(Server.class);

  private final Integer port;

  private final Vertx vertx;
  private HttpServer httpServer;
  private final StorageService storage;

  public Server(Vertx vertx, StorageService storage, Integer port) {
    this.vertx = vertx;
    this.storage = storage;
    this.port = port;
  }

  public synchronized void start() {
    httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.route(String.format("/player/:%s", NICK_NAME)).handler(ctx ->
      produceResponseObject(
        ctx,
        withString(ctx, NICK_NAME)
          .map(storage::getOrCreateplayer)
      )
    );
    router.route(String.format("/player/:%s/add", NICK_NAME)).handler(ctx -> {
      produceResponseJson(
        ctx,
        nickAndpoints(ctx).map(nickAndpoints -> {
          String nick = nickAndpoints.getValue0();
          Long points = nickAndpoints.getValue1();
          return operationResult(storage.add(nick, points));
        })
      );
    });
    router.route(String.format("/player/:%s/withdraw", NICK_NAME)).handler(ctx -> {
      produceResponseJson(
        ctx,
        nickAndpoints(ctx).map(nickAndpoints -> {
          String nick = nickAndpoints.getValue0();
          Long points = nickAndpoints.getValue1();
          return operationResult(storage.withdraw(nick, points));
        })
      );
    });
    router.route(String.format("/player/:%s/transfer", NICK_NAME)).handler(ctx -> {
      produceResponseJson(
        ctx,
        nickAndpoints(ctx).flatMap(nickAndpoints ->
          withString(ctx, TO).map(to -> {
            String from = nickAndpoints.getValue0();
            Long points = nickAndpoints.getValue1();
            return operationResult(storage.transferPoints(from, to, points));
          }))
      );
    });
    httpServer.requestHandler(router).rxListen(port).blockingGet();
    log.info("Started on port {}", port);
  }

  private Single<Pair<String, Long>> nickAndpoints(RoutingContext ctx) {
    return withString(ctx, NICK_NAME)
      .flatMap(nick -> withLong(ctx, POINTS).map(points -> new Pair<>(nick, points)));
  }

  private JsonObject errorJson(String message) {
    return new JsonObject().put("error", message);
  }

  private JsonObject operationResult(boolean res) {
    return new JsonObject().put("operation_result", res);
  }

  public synchronized void stop() {
    httpServer.rxClose().blockingGet();
  }

  private Single<Long> withLong(RoutingContext ctx, String paramName) {
    try {
      return Single.just(Long.valueOf(ctx.request().getParam(paramName)));
    } catch (NumberFormatException e) {
      return Single.error(new IllegalStateException(String.format("Param '%s' of type long is not specified", paramName)));
    }
  }

  private Single<String> withString(RoutingContext ctx, String paramName) {
    String paramValue = ctx.request().getParam(paramName);
    if (paramValue == null) {
      return Single.error(new IllegalStateException(String.format("Param '%s' of type string is not specified", paramName)));
    } else {
      return Single.just(paramValue);
    }
  }

  private void produceResponseObject(RoutingContext ctx, Single<Object> response) {
    Single<JsonObject> map = response.map(JsonObject::mapFrom);
    produceResponseJson(ctx, map);
  }

  private void produceResponseJson(RoutingContext ctx, Single<JsonObject> response) {
    response.subscribe(
      ob -> ctx.response().end(ob.encodePrettily()),
      error -> ctx.response().setStatusCode(400).end(errorJson(error.getMessage()).encodePrettily())
    );
  }
}

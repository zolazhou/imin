(ns imin.db.core
  (:require [conman.core :as conman]))

(defonce ^:dynamic *db* (conman/connect! {:jdbc-url "jdbc:h2:./h2.db"}))

(conman/bind-connection *db* "sql/queries.sql")

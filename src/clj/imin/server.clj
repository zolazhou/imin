(ns imin.server
  (:require [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.logging :as log]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.content-type :refer [wrap-content-type]]
            [ring.middleware.not-modified :refer [wrap-not-modified]]
            [ring.util.response :refer [response file-response resource-response]]
            ;; [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.adapter.jetty :refer [run-jetty]]
            [environ.core :refer [env]]
            [slingshot.slingshot :refer [try+]]
            [imin.middleware :refer [wrap-transit-response wrap-transit-params]]
            [imin.parser :as parser]
            [om.next.server :as om]
            [bidi.bidi :as bidi])
  (:gen-class))

(defonce state (atom {}))

(def routes
  ["/" {"api"  {:post {[""] :api}}
        ;; "auth" {:post {[""] :auth}}
        true   :index}])

(defn generate-response [data & [status]]
  {:status (or status 200)
   :body   data})

(def parser (om/parser {:read parser/readf :mutate parser/mutatef}))

(defn get-token [req]
  (when-let [t (get-in req [:headers "authorization"])]
    (when-let [[_ token] (str/split t #" ")]
      token)))

(def ^:dynamic *token* nil)

(defmacro with-token [token & forms]
  `(binding [*token* ~token]
     ~@forms))

(defn api [req]
  (let [env   {:state state :req (atom {}) :parser parser}
        token (get-token req)
        query (:remote (:transit-params req))]
    (log/info "================================================================")
    (log/info (with-out-str (pprint query)))
    (try+
     (generate-response (with-token token (parser env query)))
     (catch Object ex
       (log/error (with-out-str (pprint ex)))
       (generate-response ex)))))

#_(defn auth [req]
  (let [{:keys [action] :as params} (:transit-params req)
        rsp (case action
              :access-token  (api/access-token (:username params) (:password params))
              :refresh-token (api/refresh-token (:refresh-token params))
              {:error :invalid-request})]
    (generate-response rsp)))

(defn index [req]
  (assoc (resource-response "index.html" {:root "public"})
         :headers {"Content-Type" "text/html"}))

(defn handler [req]
  (let [match (bidi/match-route routes (:uri req)
                                :request-method (:request-method req))]
    (case (:handler match)
      :index (index req)
      :api   (api   req)
      ;; :auth  (auth  req)
      nil)))

(defn wrap-cljs-source
  [handler]
  (fn [request]
    (let [request (if-let [[_ uri] (re-matches #"^(.+\.cljs)&rel=\d+$" (:uri request))]
                    (assoc request :uri uri)
                    request)]
      (handler request))))

(def http-handler
  (-> handler
      (wrap-resource "public")
      (wrap-cljs-source)
      (wrap-transit-response)
      (wrap-transit-params)
      ;; (wrap-with-logger)
      (wrap-gzip)
      (wrap-content-type)
      (wrap-not-modified)))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 10555))
        host (or (env :host) "127.0.0.1")]
    (run-jetty http-handler {:port port :host host :join? false})))

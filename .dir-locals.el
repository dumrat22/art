((clojure-mode . ((cider-clojure-cli-aliases . ":dev/reloaded")
                  (cider-jack-in-nrepl-middlewares  .(cider.nrepl/cider-middleware portal.nrepl/wrap-portal)))))

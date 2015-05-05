(ns trede.core
  (:require [quil.core :as q]
            [quil.middleware :as m])
  (:gen-class))

(defn setup []
  (q/frame-rate 60)
  (q/color-mode :rgb)
  {})

(defn update-state [state]
  state)

(defn t [] (/ (q/millis) 1000.0))

(defn pulse [low high rate]
  (let [diff (- high low)
        half (/ diff 2)
        mid (+ low half)
        s (t)
        x (q/sin (* s (/ 1.0 rate)))]
    (+ mid (* x half))))

(defn cubo [x y]
  (q/with-translation [x y 0]
    (q/with-rotation [(* 0.001 (q/millis)) 0 1 0]
      (q/box 10))))

(defn cubos []
  (q/stroke-weight 1)
  (q/stroke 0)
  (q/begin-camera)
  (q/camera 0 0 (pulse -500 -600 1)
            0 0 0
            0 1 0)
  (doall (for [x (range -300 300 50)
               y (range -300 300 50)]
           (do (q/fill 250
                       (* 200 (q/cos y))
                       (* 200 (q/cos x)))
               (cubo x y))))
  (q/end-camera))

(defn fps-counter []
  (q/begin-camera)
  (q/camera)
  (q/fill 255)
  (q/no-stroke)
  (q/rect 0 0 120 30)
  (q/fill 0)
  (q/text (str "FPS: " (q/current-frame-rate)) 10 20)
  (q/end-camera))

(defn random-point []
  (let [w 50]
    [(q/random (-  w) w)
     (q/random (-  w) w)
     (q/random (-  w) w)]))

(def points1 (atom []))

(defn draw-state [state]
  (when (zero? (mod (q/frame-count) 60))
    (reset! points1 (take (q/random 3 100) (repeatedly random-point))))
  (q/background 100 100 100)
  (q/begin-camera)
  (q/camera 0 0 -100
            0 0 0
            0 1 0)
  (q/stroke 0)
  (q/fill 255)
  (q/with-translation [0 0 50]
    (q/with-rotation [(* 1.0 (t)) 0 1 0]
      (q/begin-shape :triangle-fan)
      (doseq [[x y z] @points1]
        (q/fill (* 255 (q/sin x))
                (* 255 (q/sin y))
                (* 255 (q/sin z)))
        (q/vertex x y z))
      (q/end-shape)))
  (q/end-camera)
  (fps-counter))

(defn -main [& args]
  (q/defsketch trede
    :title "THIS IS 3D"
    :size [500 500]
    :renderer :opengl
    :setup setup
    :update update-state
    :draw draw-state
    :middleware [m/fun-mode]))


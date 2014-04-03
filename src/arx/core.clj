(ns arx.core
  (:use overtone.live))

; drum sounds (sampled)
(def snare (sample (freesound-path 26903)))
(def kick (sample (freesound-path 2086)))

; drum sound (synthesized)
(definst hat [volume 1.0]
  (let [src (white-noise)
        env (env-gen (perc 0.001 0.1) :action FREE)]
    (* volume 1 src env)))

; volume modified
(defn weak-hat []
  (hat 0.3))

; metronome
(def metro (metronome 110))

(defn generate-drum-series [drum beats metro beat-number]
  (doseq [beat beats]
    (at (metro (+ beat beat-number)) (drum))))

(defn kicks [metro beat-number]
  (generate-drum-series kick [0 1 2 3] metro beat-number))

(defn snares [metro beat-number]
  (generate-drum-series snare [0.75 1.5 2.75 3.5] metro beat-number))

(defn hats [metro beat-number]
  (generate-drum-series hat [0.5 1.5 2.5 3.5] metro beat-number))

; play a typical moombahton beat
(defn simple-moom [metro beat-number]

  (doseq [drums [kicks snares hats]]
    (drums metro beat-number))

  (apply-at (metro (+ 4 beat-number)) simple-moom metro (+ 4 beat-number) []))

; drums!
(defn drums []
  (simple-moom metro (metro)))

; required by leiningen (I believe), no real use here because
; arx is 100% repl for now
(defn -main [])


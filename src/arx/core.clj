(ns arx.core
  (:use overtone.live))

; drum sounds (sampled)
(def snare (sample (freesound-path 26903)))
(def kick (sample (freesound-path 2086)))

; drum sound (synthesized)
(definst hat [volume 1.0]
  (let [src (white-noise)
        env (env-gen (perc 0.001 0.1) :action FREE)]
    (* volume 0.7 src env)))

(def tempo 170)

(def kick-beats [0 1.5 3])
(def snare-beats [1 2.5])
(def hat-beats [0 0.5 1 1.5 2 2.5 3 3.5])

; metronome
(def metro (metronome tempo))

(defn generate-drum-series [drum beats metro beat-number]
  (doseq [beat beats]
    (at (metro (+ beat beat-number)) (drum))))

(defn kicks [metro beat-number]
  (generate-drum-series kick kick-beats metro beat-number))

(defn snares [metro beat-number]
  (generate-drum-series snare snare-beats metro beat-number))

(defn hats [metro beat-number]
  (generate-drum-series hat hat-beats metro beat-number))

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


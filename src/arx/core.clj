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

; what you want is code which modifies these atoms probabilistically
; also the ability to raise or lower the "sieve"
(def kick-beats (atom [0 1.5 3]))
(def snare-beats (atom [1 2.5]))
(def hat-beats (atom [0 0.5 1 1.5 2 2.5 3 3.5])) ; FIXME: DRY?

; TODO: right fucking HERE y0
; you need a function which goes through each kick probability, determines which
; of them are less than an invocation of rand, and then translates their index
; values into the "0 1.5 3" format above, where 0 = 0 and 15 = 3.75.
(def kick-probabilities [1 0   0   0.2
                         0 0   1   0.4
                         0 0.3 0   0.4
                         1 0   0.5 0])

(def snare-probabilities  [
                            0    0   0.5  0
                            0.95 0.2 0    0
                            0.6  0   0.95 0
                            0    0.4 0    0.3
                          ])

(def hat-probabilities  [
                          0.85 0.35 0.85 0.35
                          0.85 0.35 0.85 0.35
                          0.85 0.35 0.85 0.35
                          0.85 0.35 0.85 0.35
                        ])

(defn random-drums [probabilities]
  (filter (fn [value]
              (not (nil? value)))
          (map-indexed (fn [idx prob]
                       (cond (< (rand) prob)
                                (* idx 0.25)))
                       probabilities)))

; FIXME: DRY
(defn random-kicks []
  (swap! kick-beats (fn [_] (random-drums kick-probabilities))))

(defn random-snares []
  (swap! snare-beats (fn [_] (random-drums snare-probabilities))))

(defn random-hats []
  (swap! hat-beats (fn [_] (random-drums hat-probabilities))))

(defn random-beat []
  (random-kicks)
  (random-snares)
  (random-hats))

; the following three functions enable live-coding. to plug in new patterns,
; write code like this in the REPL:
;
;   (kicks [0 2 3.75]) ; for example
;
; in addition to being a nice idiom for coding live, this is good setup for
; the archaeopteryx-style generative breakbeats I have planned...

; FIXME: DRY
; might also be wiser to make these atoms too
(defn kicks [beats]
  (swap! kick-beats (fn [_] beats)))

(defn snares [beats]
  (swap! snare-beats (fn [_] beats)))

(defn hats [beats]
  (swap! hat-beats (fn [_] beats)))

; metronome
(def metro (atom (metronome 170)))

(defn generate-drum-series [drum beats beat-number]
  (doseq [beat beats]
    (at (@metro (+ beat beat-number)) (drum))))

; FIXME: DRY. the following three functions are nearly identical, and highly
; repetitious.
(defn generate-kicks [beat-number]
  (generate-drum-series kick @kick-beats beat-number))

(defn generate-snares [beat-number]
  (generate-drum-series snare @snare-beats beat-number))

(defn generate-hats [beat-number]
  (generate-drum-series hat @hat-beats beat-number))

(defn play-beat [beat-number]

    (doseq [generate-drums [generate-kicks generate-snares generate-hats]]
      (generate-drums beat-number))

    (apply-at (@metro (+ 4 beat-number)) play-beat (+ 4 beat-number) []))

(defn drums []
  (play-beat (@metro)))

; use these to do paint-by-numbers live-coding; just fire off (variation)
; or (main-loop) to switch from one to the other in the REPL
(defn main-loop []
  (kicks [0 1.5 3])
  (snares [1 2.5])
  (hats [0 0.5 1 1.5 2 2.5 3 3.5])) ; FIXME: DRY?

(defn variation []
  (kicks [0 2 2.5])
  (snares [1 3])
  (hats [0 0.25 0.5 0.75 1 1.25 1.5 1.75 2 2.25 2.5 2.75 3 3.25 3.5 3.75])) ; FIXME: DRY?

; use this to change tempo live. restarts loop >.<
(defn change-tempo [bpm]
  (swap! metro (fn [_] (metronome bpm)))
  (stop)
  (drums))

; required by leiningen (I believe), no real use here because
; arx is 100% repl for now
(defn -main [])


# aquarium-controller
Controller voor aquarium

## interface

### section 1 - display status

  - tl
  - led1
  - led2
  
### section 2 - controls

  - time control (slider)
  - play/pause
  - override
  - speed
  - update rate
  
## API

`GET /state`:
    return stream of state. Updated whenever state changes

`GET /time`:
    return current simulator time

`GET /sim/state`:
  return a summary of the simulation state. contains
  - override: boolean
  - speed: integer
  - state: PLAY|PAUSE

`POST /sim/reset`:
  Resume simulation using current time and speed: 1
  
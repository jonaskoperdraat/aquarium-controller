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
  - rate: float
  - state: PLAY|PAUSE

`GET /sim/override`
  returns whether override is set or not

`POST /sim/override/on`
  enable override
 
`POST /sim/override/off`
  disable override
  
`GET /sim/state`
  get current simulation state (PLAY/PAUSE)

`POST /sim/{PLAY|PAUSE}`
  change simulation state
  
`GET /sim/rate`
  returns the current imulation rate (double)
  
`POST /sim/rate/{rate}`
  sets the the current simulation rate (double) 
  
  
package be.kdg.gameservice.round.controller;

import be.kdg.gameservice.round.controller.dto.ActDTO;
import be.kdg.gameservice.round.exception.RoundException;
import be.kdg.gameservice.round.model.ActType;
import be.kdg.gameservice.round.service.api.RoundService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * This API is used for API connections that have somthing to do
 * with the games of poker
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RoundApiController {
    private final ModelMapper modelMapper;
    private final RoundService roundService;

    /**
     * Gets all the possible acts that can be played for a specific player
     * in a specific round.
     *
     * @param roundId The id of the round
     * @param playerId The id of the player.
     * @return Status code 200 if the get succeeded.
     * @throws RoundException Rerouted to handler.
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/rounds/{roundId}/players/{playerId}/possible-acts")
    public ResponseEntity<ActType[]> getPossibleActs(@PathVariable int roundId,
                                                     @PathVariable int playerId) throws RoundException {
        List<ActType> actTypes = roundService.getPossibleActs(roundId, playerId);
        return new ResponseEntity<>(modelMapper.map(actTypes, ActType[].class), HttpStatus.OK);
    }

    /**
     * Saves an act that is played by a player in the back end.
     * The act is validated in the round service for a last time.
     *
     * @param actDTO The information needed to make a new Act.
     * @return Status code 201 if the post succeeded.
     * @throws RoundException Rerouted to handler.
     * @see ActDTO
     */
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/rounds/{roundId}/players/{playerId}/acts")
    public ResponseEntity<ActDTO> doAct(@RequestBody @Valid ActDTO actDTO, @PathVariable int roundId,
                                        @PathVariable int playerId) throws RoundException {
        roundService.saveAct(roundId, playerId, actDTO.getType(), actDTO.getPhase(), actDTO.getBet());
        return new ResponseEntity<>(actDTO, HttpStatus.CREATED);
    }
}

package demo.mable.global.exception;

import demo.mable.character.exception.*;
import demo.mable.game.exception.*;
import demo.mable.room.exception.RoomNotJoinableException;
import demo.mable.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(CharacterRankMismatchException.class)
    public ResponseEntity<CharacterRankMismatchException> handleCharacterRankMismatch() {
        return ResponseEntity.badRequest().body(new CharacterRankMismatchException());
    }

    @ExceptionHandler(RoomNotJoinableException.class)
    public ResponseEntity<RoomNotJoinableException> RoomNotJoinable() {
        return ResponseEntity.badRequest().body(new RoomNotJoinableException());
    }

    @ExceptionHandler(UserSelectedCharacterNotFoundException.class)
    public ResponseEntity<UserSelectedCharacterNotFoundException> UserSelectedCharacterNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserSelectedCharacterNotFoundException());
    }

    @ExceptionHandler(UserIdRequiredException.class)
    public ResponseEntity<UserIdRequiredException> userIdRequiredException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new UserIdRequiredException());
    }

    @ExceptionHandler(CharacterIdRequiredException.class)
    public ResponseEntity<CharacterIdRequiredException> CharacterIdRequiredException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CharacterIdRequiredException());
    }

    @ExceptionHandler(CharacterCardNotOwnedByUserException.class)
    public ResponseEntity<CharacterCardNotOwnedByUserException> CharacterCardNotOwnedByUserException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CharacterCardNotOwnedByUserException());
    }

    @ExceptionHandler(MissingCharacterSynthesisDataException.class)
    public ResponseEntity<MissingCharacterSynthesisDataException> MissingCharacterSynthesisDataException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MissingCharacterSynthesisDataException());
    }

    @ExceptionHandler(InvalidSynthesisMaterialCountException.class)
    public ResponseEntity<InvalidSynthesisMaterialCountException> InvalidSynthesisMaterialCountException() {
        return ResponseEntity.badRequest().body(new InvalidSynthesisMaterialCountException());
    }
    @ExceptionHandler(DuplicateSynthesisMaterialException.class)
    public ResponseEntity<DuplicateSynthesisMaterialException> DuplicateSynthesisMaterialException() {
        return ResponseEntity.badRequest().body(new DuplicateSynthesisMaterialException());
    }
    @ExceptionHandler(UnsupportedCharacterRankException.class)
    public ResponseEntity<UnsupportedCharacterRankException> UnsupportedCharacterRankException() {
        return ResponseEntity.badRequest().body(new UnsupportedCharacterRankException());
    }

    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<GameNotFoundException> GameNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GameNotFoundException());
    }

    @ExceptionHandler(GamePlayerNotFoundException.class)
    public ResponseEntity<GamePlayerNotFoundException> GamePlayerNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new GamePlayerNotFoundException());
    }

    @ExceptionHandler(GamePhaseException.class)
    public ResponseEntity<GamePhaseException> GamePhaseException() {
        return ResponseEntity.badRequest().body(new GamePhaseException());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<UserNotFoundException> UserNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new UserNotFoundException());
    }

    @ExceptionHandler(TurnOrderCardAlreadySelectedException.class)
    public ResponseEntity<TurnOrderCardAlreadySelectedException> TurnOrderCardAlreadySelectedException() {
        return ResponseEntity.badRequest().body(new TurnOrderCardAlreadySelectedException());
    }

    @ExceptionHandler(PlayerNotOnTileException.class)
    public ResponseEntity<PlayerNotOnTileException> PlayerNotOnTileException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new PlayerNotOnTileException());
    }

    @ExceptionHandler(LandmarkAcquisitionNotAllowedException.class)
    public ResponseEntity<LandmarkAcquisitionNotAllowedException> LandmarkAcquisitionNotAllowedException() {
        return ResponseEntity.badRequest().body(new LandmarkAcquisitionNotAllowedException());
    }

    @ExceptionHandler(GameRoomNotWaitingException.class)
    public ResponseEntity<GameRoomNotWaitingException> GameRoomNotWaitingException() {
        return ResponseEntity.badRequest().body(new GameRoomNotWaitingException());
    }

    @ExceptionHandler(NotAllPlayersReadyException.class)
    public ResponseEntity<NotAllPlayersReadyException> NotAllPlayersReadyException() {
        return ResponseEntity.badRequest().body(new NotAllPlayersReadyException());
    }

    @ExceptionHandler(NotRoomHostException.class)
    public ResponseEntity<NotRoomHostException> NotRoomHostException() {
        return ResponseEntity.badRequest().body(new NotRoomHostException());
    }

    @ExceptionHandler(InsufficientPlayersException.class)
    public ResponseEntity<InsufficientPlayersException> InsufficientPlayersException() {
        return ResponseEntity.badRequest().body(new InsufficientPlayersException());
    }

    @ExceptionHandler(NotCurrentTurnPlayerException.class)
    public ResponseEntity<NotCurrentTurnPlayerException> NotCurrentTurnPlayerException() {
        return ResponseEntity.badRequest().body(new NotCurrentTurnPlayerException());
    }

    @ExceptionHandler(GameNotWaitingForDiceException.class)
    public ResponseEntity<GameNotWaitingForDiceException> GameNotWaitingForDiceException() {
        return ResponseEntity.badRequest().body(new GameNotWaitingForDiceException());
    }

    @ExceptionHandler(TileNotFoundException.class)
    public ResponseEntity<TileNotFoundException> TileNotFoundException() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TileNotFoundException());
    }

    @ExceptionHandler(InvalidGameStateException.class)
    public ResponseEntity<InvalidGameStateException> InvalidGameStateException() {
        return ResponseEntity.badRequest().body(new InvalidGameStateException());
    }

    @ExceptionHandler(AuthenticatedPlayerMismatchException.class)
    public ResponseEntity<AuthenticatedPlayerMismatchException> AuthenticatedPlayerMismatchException() {
        return ResponseEntity.badRequest().body(new AuthenticatedPlayerMismatchException());
    }

    @ExceptionHandler(PropertyOwnerCannotPayTollException.class)
    public ResponseEntity<PropertyOwnerCannotPayTollException> PropertyOwnerCannotPayTollException() {
        return ResponseEntity.badRequest().body(new PropertyOwnerCannotPayTollException());
    }

    @ExceptionHandler(NextTurnPlayerNotFoundException.class)
    public ResponseEntity<NextTurnPlayerNotFoundException> NextTurnPlayerNotFoundException() {
        return ResponseEntity.badRequest().body(new NextTurnPlayerNotFoundException());
    }

    @ExceptionHandler(InvalidBuildingLevelException.class)
    public ResponseEntity<InvalidBuildingLevelException> InvalidBuildingLevelException() {
        return ResponseEntity.badRequest().body(new InvalidBuildingLevelException());
    }

    @ExceptionHandler(GameNotWaitingForTrainDestinationException.class)
    public ResponseEntity<GameNotWaitingForTrainDestinationException> GameNotWaitingForTrainDestinationException() {
        return ResponseEntity.badRequest().body(new GameNotWaitingForTrainDestinationException());
    }

    @ExceptionHandler(InvalidMovementPathException.class)
    public ResponseEntity<InvalidMovementPathException> InvalidMovementPathException() {
        return ResponseEntity.badRequest().body(new InvalidMovementPathException());
    }

    @ExceptionHandler(GameNotWaitingForSkillBuildTargetException.class)
    public ResponseEntity<GameNotWaitingForSkillBuildTargetException> GameNotWaitingForSkillBuildTargetException() {
        return ResponseEntity.badRequest().body(new GameNotWaitingForSkillBuildTargetException());
    }

    @ExceptionHandler(InvalidTileException.class)
    public ResponseEntity<InvalidTileException> InvalidTileException() {
        return ResponseEntity.badRequest().body(new InvalidTileException());
    }

    @ExceptionHandler(NotPropertyOwnerException.class)
    public ResponseEntity<NotPropertyOwnerException> NotPropertyOwnerException() {
        return ResponseEntity.badRequest().body(new NotPropertyOwnerException());
    }

    @ExceptionHandler(BuildingSelectionRequiredException.class)
    public ResponseEntity<BuildingSelectionRequiredException> BuildingSelectionRequiredException() {
        return ResponseEntity.badRequest().body(new BuildingSelectionRequiredException());
    }


    @ExceptionHandler(BuildingAlreadyConstructedException.class)
    public ResponseEntity<BuildingAlreadyConstructedException> BuildingAlreadyConstructedException() {
        return ResponseEntity.badRequest().body(new BuildingAlreadyConstructedException());
    }

    @ExceptionHandler(Exception.class)
    public void Exception(Exception e) {
        e.printStackTrace();
    }
}

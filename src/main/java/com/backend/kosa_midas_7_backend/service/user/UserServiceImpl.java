package com.backend.kosa_midas_7_backend.service.user;

import com.backend.kosa_midas_7_backend.entity.dto.user.*;
import com.backend.kosa_midas_7_backend.entity.user.User;
import com.backend.kosa_midas_7_backend.entity.user.repository.UserRepository;
import com.backend.kosa_midas_7_backend.service.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final MailService mailService;

    // GET
    @Override
    public ResponseEntity<User> findUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return new ResponseEntity<>(user.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<User>> findAllUser() {
        List<User> userList = userRepository.findAll();

        if (userList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(userList, HttpStatus.OK);
        }
    }

    // POST
    @Override
    public ResponseEntity<HttpStatus> findPassword(FindPasswordDto findPasswordDto) throws Exception {
        String accountId = findPasswordDto.getAccountId();

        User user = userRepository.findByAccountId(accountId).orElseThrow(RuntimeException::new);
        String username = user.getUserName();
        String email = user.getEmail();

        EmailAuthDto emailAuthDto = new EmailAuthDto(username, email);

        return mailService.sendAuthMail(emailAuthDto);
    }

    @Override
    public ResponseEntity<String> findIdCheckAuthCode(CheckEmailAuthCodeDto checkEmailAuthCodeDto) {
        try {
            if (checkEmailAuthCode(checkEmailAuthCodeDto)) {
                String email = checkEmailAuthCodeDto.getEmail();
                String accountId = userRepository.findByEmail(email).getAccountId();

                return new ResponseEntity<>(accountId, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        } catch (Exception exception) {
            log.info("error: {}", exception.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Boolean> findPasswordCheckAuthCode(FindPasswordCheck findPasswordCheck) {
        String accountId = findPasswordCheck.getAccountId();
        Optional<User> user = userRepository.findByAccountId(accountId);

        if (user.isPresent()) {
            String email = user.get().getEmail();
            String code = findPasswordCheck.getCode();

            if (mailService.checkAuthCode(email, code)) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.CONFLICT);
            }
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    // PUT
    @Override
    public ResponseEntity<User> changeCoreTime(ChangeCoreTimeDto changeCoreTimeDto) {
        Long userId = changeCoreTimeDto.getUserId();
        Optional<User> user = userRepository.findById(userId);

        if (user.isPresent()) {
            User userEntity = user.get();
            userEntity.setCoreTimeStart(changeCoreTimeDto.getCoreTimeStart());
            userEntity.setCoreTimeEnd(changeCoreTimeDto.getCoreTimeEnd());

            userRepository.save(userEntity);

            return new ResponseEntity<>(userEntity, HttpStatus.OK);
        } else {
            return  new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    // DELETE

    // ELSE
    @Override
    public Boolean checkEmailAuthCode(CheckEmailAuthCodeDto checkEmailAuthCodeDto) {
        String email = checkEmailAuthCodeDto.getEmail();
        String code = checkEmailAuthCodeDto.getCode();

        return mailService.checkAuthCode(email, code);
    }

}

package kkukmoa.kkukmoa.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import kkukmoa.kkukmoa.owner.dto.request.OwnerRegisterRequest;
import kkukmoa.kkukmoa.owner.dto.request.OwnerSignupRequest;
import kkukmoa.kkukmoa.owner.service.OwnerCommandService;
import kkukmoa.kkukmoa.user.domain.User;
import kkukmoa.kkukmoa.user.repository.UserRepository;
import lombok.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/dummy")
@RequiredArgsConstructor
public class DummyController {

    private final OwnerCommandService ownerCommandService;

    private final UserRepository userRepository;

    // 유저들은 그냥 직접 생성 ( 사장님으로 )
    // 가게 만들고

    @PostMapping("/stores")
    @Operation(summary = "가게 & 사장 계정 더미데이터")
    public void putStoreDummy(@RequestBody DummyList dto){

        // 사장님 생성
        dto.getRequest()
                .forEach( request ->  ownerCommandService.registerLocalOwner(request.getOwnerSignupRequest()));

        // 사장님 조회
        List<String> idList = dto.getRequest().stream().map(request -> request.getOwnerSignupRequest().getEmail()).toList();
        List<User> users = userRepository.findByEmailIn(idList);

        // 가게 생성
        List<OwnerRegisterRequest> storeRequestList = dto.getRequest().stream().map(DummyDTO::getOwnerRegisterRequest).toList();

        if(users.size() != storeRequestList.size()){
            System.out.println("user.size() != storeRequestList.size()");
        }

        for(int i = 0; i < users.size(); i++){

            User user = users.get(i);
            OwnerRegisterRequest storeRequest = storeRequestList.get(i);
            ownerCommandService.applyStoreRegistration(user, storeRequest);
            System.out.println("이메일 : " + user.getEmail() + "가게명 : " + storeRequest.getStoreName() + " 저장 성공! ");

        }

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DummyList{

        List<DummyDTO> request;

    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DummyDTO{

        OwnerSignupRequest ownerSignupRequest;
        OwnerRegisterRequest ownerRegisterRequest;

    }


}

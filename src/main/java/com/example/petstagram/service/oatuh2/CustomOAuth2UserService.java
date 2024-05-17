package com.example.petstagram.service.oatuh2;


import com.example.petstagram.dto.CustomOAuth2User;
import com.example.petstagram.dto.FacebookResponse;
import com.example.petstagram.dto.OAuth2Response;
import com.example.petstagram.entity.userEntity.UserEntity;
import com.example.petstagram.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

//        if (registrationId.equals("naver")) {
//
//            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
//        }
//        else if (registrationId.equals("google")) {
//
//            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
//        }
//        else {
//
//            return null;
//        }

        if (registrationId.equals("facebook")) {
            oAuth2Response = new FacebookResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        UserEntity existData = userRepository.findByName(username);

        String role = "ROLE_USER";
        if (existData == null) {

            UserEntity userEntity = new UserEntity();

            userEntity.setName(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setRole(role);

            userRepository.save(userEntity);
        }
        else {

            existData.setName(username);
            existData.setEmail(oAuth2Response.getEmail());

            role = existData.getRole();

            userRepository.save(existData);
        }
        return new CustomOAuth2User(oAuth2Response, role);
    }
}

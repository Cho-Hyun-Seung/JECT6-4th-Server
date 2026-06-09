package com.ject6.boost.presentation.user.controller.docs;

import com.ject6.boost.presentation.common.dto.ApiResponse;
import com.ject6.boost.presentation.common.security.authentication.AuthenticatedUser;
import com.ject6.boost.presentation.user.dto.BlogLinkRequest;
import com.ject6.boost.presentation.user.dto.BlogLinkResponse;
import com.ject6.boost.presentation.user.dto.NicknameCheckResponse;
import com.ject6.boost.presentation.user.dto.ProfileRequest;
import com.ject6.boost.presentation.user.dto.ProfileResponse;
import com.ject6.boost.presentation.user.dto.RandomNicknameResponse;
import com.ject6.boost.presentation.user.dto.UserMeResponse;
import com.ject6.boost.presentation.user.dto.UserProfileUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

public interface UserApi {

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 프로필, 관심 카테고리, 활동 유형, 지역, 블로그 정보를 조회합니다.")
    ApiResponse<UserMeResponse> getMe(AuthenticatedUser principal);

    @Operation(summary = "프로필 부분 수정", description = "닉네임, 관심 카테고리, 활동 유형, 지역 중 전달된 필드만 수정합니다.")
    ApiResponse<UserMeResponse> updateProfile(
            AuthenticatedUser principal,
            UserProfileUpdateRequest request
    );

    @Operation(summary = "프로필 생성", description = "현재 로그인한 사용자의 닉네임, 관심 카테고리, 활동 유형, 지역 정보를 저장하고 프로필을 완료합니다.")
    ApiResponse<ProfileResponse> createProfile(
            AuthenticatedUser principal,
            ProfileRequest request
    );

    @Operation(summary = "블로그 연동", description = "블로그 포스팅 5개 이상을 검증한 뒤 블로그를 연동합니다.")
    ApiResponse<BlogLinkResponse> linkBlog(
            AuthenticatedUser principal,
            BlogLinkRequest request
    );

    @Operation(summary = "닉네임 중복 확인", description = "입력한 닉네임을 사용할 수 있는지 확인합니다.")
    ApiResponse<NicknameCheckResponse> checkNickname(String nickname);

    @Operation(summary = "랜덤 닉네임 생성", description = "사용 가능한 랜덤 닉네임 후보를 생성합니다.")
    ApiResponse<RandomNicknameResponse> generateRandomNickname();

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리하고 refresh token session을 제거합니다.")
    ApiResponse<Void> withdraw(
            AuthenticatedUser principal,
            String authorization,
            String refreshToken,
            HttpServletResponse response
    );
}
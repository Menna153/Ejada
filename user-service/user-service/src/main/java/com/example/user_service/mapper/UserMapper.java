package com.example.user_service.mapper;

import com.example.user_service.dto.CreateUserRequest;
import com.example.user_service.dto.CreateUserResponse;
import com.example.user_service.dto.GetUserProfile;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UserMapper {
    @Mapping(target="userId", ignore = true)
    Users fromCreateUserRequest(CreateUserRequest createUserRequest);

    @Mapping(target="message", ignore = true)
    CreateUserResponse toCreateUserResponse(Users users);

    GetUserProfile toUserProfile(Users users);
    LoginResponse toLoginResponse(Users users);
}

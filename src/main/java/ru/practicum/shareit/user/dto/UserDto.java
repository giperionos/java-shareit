package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(groups = {UserCreateMarker.class}, message = "Не указано имя пользователя.")
    private String name;

    @NotNull(groups = {UserCreateMarker.class}, message = "Не указан email пользователя.")
    @Email(groups = {UserCreateMarker.class, UserUpdateMarker.class}, message = "Указанный email пользователя не соответствует формату email.")
    private String email;
}

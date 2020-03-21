package de.qaware.mercury.mercury.rest.shop;

import de.qaware.mercury.mercury.business.login.LoginException;
import de.qaware.mercury.mercury.business.login.ShopCreationToken;
import de.qaware.mercury.mercury.business.login.TokenService;
import de.qaware.mercury.mercury.business.shop.Shop;
import de.qaware.mercury.mercury.business.shop.ShopService;
import de.qaware.mercury.mercury.rest.plumbing.authentication.AuthenticationHelper;
import de.qaware.mercury.mercury.rest.plumbing.authentication.InvalidCredentialsRestException;
import de.qaware.mercury.mercury.rest.shop.dto.SendCreateLinkDto;
import de.qaware.mercury.mercury.rest.shop.dto.ShopCreateDto;
import de.qaware.mercury.mercury.rest.shop.dto.ShopListDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@RequestMapping(value = "/api/shop", produces = MediaType.APPLICATION_JSON_VALUE)
class ShopController {
    private final ShopService shopService;
    private final TokenService tokenService;
    private final AuthenticationHelper authenticationHelper;

    @PostMapping(path = "/send-create-link", consumes = MediaType.APPLICATION_JSON_VALUE)
    void sendCreateLink(@RequestBody SendCreateLinkDto request) {
        shopService.sendCreateLink(request.getEmail());
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    ShopCreateDto createShop(@RequestBody ShopCreateDto shop, @RequestParam String token) {
        // The token is taken from the link which the user got with email
        // It contains the email address, and is used to verify that the user really has access to this email address
        String email;
        try {
            email = tokenService.verifyShopCreationToken(ShopCreationToken.of(token));
        } catch (LoginException e) {
            throw new InvalidCredentialsRestException(e.getMessage());
        }

        return ShopCreateDto.of(shopService.create(
            shop.getName(),
            shop.getOwnerName(),
            email, // Email is not taken from request body, but from the token which was in the email link
            shop.getStreet(),
            shop.getZipCode(),
            shop.getCity(),
            shop.getAddressSupplement(),
            shop.getContactTypes()
        ));
    }

    @PutMapping(path = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    ShopCreateDto updateShop(@RequestBody ShopCreateDto shop, HttpServletRequest request) {
        // The token is taken from the link which the user got with email
        // It contains the email address, and is used to verify that the user really has access to this email address
        Shop authenticatedShop = authenticationHelper.authenticateShop(request);

        return ShopCreateDto.of(shopService.update(
            authenticatedShop.getId(),
            shop.getName(),
            shop.getOwnerName(),
            authenticatedShop.getEmail(), // Email is not taken from request body, but from the token which was in the email link
            shop.getStreet(),
            shop.getZipCode(),
            shop.getCity(),
            shop.getAddressSupplement(),
            shop.getContactTypes(),
            authenticatedShop.isEnabled()
        ));
    }

    @GetMapping("nearby")
    ShopListDto listNearby(@RequestParam String location) {
        return ShopListDto.of(shopService.findNearby(location));
    }
}

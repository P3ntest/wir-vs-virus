package de.qaware.mercury.mercury.rest.shop;

import de.qaware.mercury.mercury.business.admin.Admin;
import de.qaware.mercury.mercury.business.login.LoginException;
import de.qaware.mercury.mercury.business.shop.ContactType;
import de.qaware.mercury.mercury.business.shop.Shop;
import de.qaware.mercury.mercury.business.shop.ShopNotFoundException;
import de.qaware.mercury.mercury.business.shop.ShopService;
import de.qaware.mercury.mercury.business.shop.ShopUpdate;
import de.qaware.mercury.mercury.rest.plumbing.authentication.AuthenticationHelper;
import de.qaware.mercury.mercury.rest.shop.dto.ShopAdminDto;
import de.qaware.mercury.mercury.rest.shop.dto.ShopsAdminDto;
import de.qaware.mercury.mercury.rest.shop.dto.UpdateShopRequestDto;
import de.qaware.mercury.mercury.util.Maps;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/admin/shop", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Validated
class ShopAdminController {
    private final ShopService shopService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping
    ShopsAdminDto listAll(HttpServletRequest request) throws LoginException {
        authenticationHelper.authenticateAdmin(request);

        return ShopsAdminDto.of(shopService.listAll());
    }

    @PutMapping(path = "/{id}/enable")
    void changeEnabled(@PathVariable String id, @RequestParam boolean enabled, HttpServletRequest request) throws ShopNotFoundException, LoginException {
        Admin admin = authenticationHelper.authenticateAdmin(request);
        Shop.Id shopId = Shop.Id.parse(id);

        log.info("Admin '{}' changed enabled flag from shop {} to {}", admin.getEmail(), shopId, enabled);
        shopService.changeEnabled(shopId, enabled);
    }

    @PutMapping(path = "/{id}")
    ShopAdminDto update(@PathVariable String id, @Valid @RequestBody UpdateShopRequestDto request, HttpServletRequest servletRequest) throws ShopNotFoundException, LoginException {
        Admin admin = authenticationHelper.authenticateAdmin(servletRequest);
        Shop.Id shopId = Shop.Id.parse(id);

        Shop shop = shopService.findById(shopId);
        if (shop == null) {
            throw new ShopNotFoundException(shopId);
        }

        log.info("Admin {} updated shop '{}'", admin.getEmail(), shop.getName());
        return ShopAdminDto.of(shopService.update(shop, new ShopUpdate(
            request.getName(),
            request.getOwnerName(),
            request.getStreet(),
            request.getZipCode(),
            request.getCity(),
            request.getAddressSupplement(),
            request.getDetails(),
            request.getWebsite(),
            Maps.mapKeys(request.getContactTypes(), ContactType::parse),
            request.getSlots().toSlots()
        )));
    }

    @DeleteMapping(path = "/{id}")
    void delete(@PathVariable String id, HttpServletRequest request) throws ShopNotFoundException, LoginException {
        Admin admin = authenticationHelper.authenticateAdmin(request);
        Shop.Id shopId = Shop.Id.parse(id);

        log.info("Admin {} deleted shop {}", admin.getEmail(), shopId);
        shopService.delete(shopId);
    }
}

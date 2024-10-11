package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Category;
import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class VendorControllerTest {

    VendorRepository vendorRepository;
    VendorController vendorController;
    WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        vendorRepository = mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void testList() {
        given(vendorRepository.findAll()).willReturn(Flux.just(Vendor.builder().firstName("Joe").lastName("Buck").build(),
                Vendor.builder().firstName("Kayne").lastName("West").build()));

        webTestClient.get().uri(VendorController.API_V1_VENDORS)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void testGetById() {
        given(vendorRepository.findById(anyString())).willReturn(Mono.just(Vendor.builder().firstName("Kayne").lastName("West").build()));

        webTestClient.get().uri(VendorController.API_V1_VENDORS + "/someid")
                .exchange()
                .expectBodyList(Vendor.class);

    }

    @Test
    public void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("Jim").lastName("Carey").build());

        webTestClient.post().uri(VendorController.API_V1_VENDORS)
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }
}
package com.kis.health.web.rest;

import com.kis.health.KishealthApp;

import com.kis.health.domain.Address;
import com.kis.health.repository.AddressRepository;
import com.kis.health.service.AddressService;
import com.kis.health.web.rest.errors.ExceptionTranslator;
import com.kis.health.service.dto.AddressCriteria;
import com.kis.health.service.AddressQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.kis.health.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the AddressResource REST controller.
 *
 * @see AddressResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = KishealthApp.class)
public class AddressResourceIntTest {

    private static final String DEFAULT_ADDRESS_LINE = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_NEIGHBORHOOD = "AAAAAAAAAA";
    private static final String UPDATED_NEIGHBORHOOD = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_STATE = "AAAAAAAAAA";
    private static final String UPDATED_STATE = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_ZIP_CODE = "AAAAAAAAAA";
    private static final String UPDATED_ZIP_CODE = "BBBBBBBBBB";

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressQueryService addressQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAddressMockMvc;

    private Address address;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final AddressResource addressResource = new AddressResource(addressService, addressQueryService);
        this.restAddressMockMvc = MockMvcBuilders.standaloneSetup(addressResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Address createEntity(EntityManager em) {
        Address address = new Address()
            .addressLine(DEFAULT_ADDRESS_LINE)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .neighborhood(DEFAULT_NEIGHBORHOOD)
            .city(DEFAULT_CITY)
            .state(DEFAULT_STATE)
            .country(DEFAULT_COUNTRY)
            .zipCode(DEFAULT_ZIP_CODE);
        return address;
    }

    @Before
    public void initTest() {
        address = createEntity(em);
    }

    @Test
    @Transactional
    public void createAddress() throws Exception {
        int databaseSizeBeforeCreate = addressRepository.findAll().size();

        // Create the Address
        restAddressMockMvc.perform(post("/api/addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(address)))
            .andExpect(status().isCreated());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate + 1);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getAddressLine()).isEqualTo(DEFAULT_ADDRESS_LINE);
        assertThat(testAddress.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testAddress.getNeighborhood()).isEqualTo(DEFAULT_NEIGHBORHOOD);
        assertThat(testAddress.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testAddress.getState()).isEqualTo(DEFAULT_STATE);
        assertThat(testAddress.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testAddress.getZipCode()).isEqualTo(DEFAULT_ZIP_CODE);
    }

    @Test
    @Transactional
    public void createAddressWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = addressRepository.findAll().size();

        // Create the Address with an existing ID
        address.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAddressMockMvc.perform(post("/api/addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(address)))
            .andExpect(status().isBadRequest());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllAddresses() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList
        restAddressMockMvc.perform(get("/api/addresses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(address.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressLine").value(hasItem(DEFAULT_ADDRESS_LINE.toString())))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2.toString())))
            .andExpect(jsonPath("$.[*].neighborhood").value(hasItem(DEFAULT_NEIGHBORHOOD.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].zipCode").value(hasItem(DEFAULT_ZIP_CODE.toString())));
    }

    @Test
    @Transactional
    public void getAddress() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get the address
        restAddressMockMvc.perform(get("/api/addresses/{id}", address.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(address.getId().intValue()))
            .andExpect(jsonPath("$.addressLine").value(DEFAULT_ADDRESS_LINE.toString()))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2.toString()))
            .andExpect(jsonPath("$.neighborhood").value(DEFAULT_NEIGHBORHOOD.toString()))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY.toString()))
            .andExpect(jsonPath("$.state").value(DEFAULT_STATE.toString()))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY.toString()))
            .andExpect(jsonPath("$.zipCode").value(DEFAULT_ZIP_CODE.toString()));
    }

    @Test
    @Transactional
    public void getAllAddressesByAddressLineIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine equals to DEFAULT_ADDRESS_LINE
        defaultAddressShouldBeFound("addressLine.equals=" + DEFAULT_ADDRESS_LINE);

        // Get all the addressList where addressLine equals to UPDATED_ADDRESS_LINE
        defaultAddressShouldNotBeFound("addressLine.equals=" + UPDATED_ADDRESS_LINE);
    }

    @Test
    @Transactional
    public void getAllAddressesByAddressLineIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine in DEFAULT_ADDRESS_LINE or UPDATED_ADDRESS_LINE
        defaultAddressShouldBeFound("addressLine.in=" + DEFAULT_ADDRESS_LINE + "," + UPDATED_ADDRESS_LINE);

        // Get all the addressList where addressLine equals to UPDATED_ADDRESS_LINE
        defaultAddressShouldNotBeFound("addressLine.in=" + UPDATED_ADDRESS_LINE);
    }

    @Test
    @Transactional
    public void getAllAddressesByAddressLineIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine is not null
        defaultAddressShouldBeFound("addressLine.specified=true");

        // Get all the addressList where addressLine is null
        defaultAddressShouldNotBeFound("addressLine.specified=false");
    }

    @Test
    @Transactional
    public void getAllAddressesByAddressLine2IsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 equals to DEFAULT_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.equals=" + DEFAULT_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.equals=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    public void getAllAddressesByAddressLine2IsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 in DEFAULT_ADDRESS_LINE_2 or UPDATED_ADDRESS_LINE_2
        defaultAddressShouldBeFound("addressLine2.in=" + DEFAULT_ADDRESS_LINE_2 + "," + UPDATED_ADDRESS_LINE_2);

        // Get all the addressList where addressLine2 equals to UPDATED_ADDRESS_LINE_2
        defaultAddressShouldNotBeFound("addressLine2.in=" + UPDATED_ADDRESS_LINE_2);
    }

    @Test
    @Transactional
    public void getAllAddressesByAddressLine2IsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where addressLine2 is not null
        defaultAddressShouldBeFound("addressLine2.specified=true");

        // Get all the addressList where addressLine2 is null
        defaultAddressShouldNotBeFound("addressLine2.specified=false");
    }

    @Test
    @Transactional
    public void getAllAddressesByNeighborhoodIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where neighborhood equals to DEFAULT_NEIGHBORHOOD
        defaultAddressShouldBeFound("neighborhood.equals=" + DEFAULT_NEIGHBORHOOD);

        // Get all the addressList where neighborhood equals to UPDATED_NEIGHBORHOOD
        defaultAddressShouldNotBeFound("neighborhood.equals=" + UPDATED_NEIGHBORHOOD);
    }

    @Test
    @Transactional
    public void getAllAddressesByNeighborhoodIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where neighborhood in DEFAULT_NEIGHBORHOOD or UPDATED_NEIGHBORHOOD
        defaultAddressShouldBeFound("neighborhood.in=" + DEFAULT_NEIGHBORHOOD + "," + UPDATED_NEIGHBORHOOD);

        // Get all the addressList where neighborhood equals to UPDATED_NEIGHBORHOOD
        defaultAddressShouldNotBeFound("neighborhood.in=" + UPDATED_NEIGHBORHOOD);
    }

    @Test
    @Transactional
    public void getAllAddressesByNeighborhoodIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where neighborhood is not null
        defaultAddressShouldBeFound("neighborhood.specified=true");

        // Get all the addressList where neighborhood is null
        defaultAddressShouldNotBeFound("neighborhood.specified=false");
    }

    @Test
    @Transactional
    public void getAllAddressesByCityIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where city equals to DEFAULT_CITY
        defaultAddressShouldBeFound("city.equals=" + DEFAULT_CITY);

        // Get all the addressList where city equals to UPDATED_CITY
        defaultAddressShouldNotBeFound("city.equals=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    public void getAllAddressesByCityIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where city in DEFAULT_CITY or UPDATED_CITY
        defaultAddressShouldBeFound("city.in=" + DEFAULT_CITY + "," + UPDATED_CITY);

        // Get all the addressList where city equals to UPDATED_CITY
        defaultAddressShouldNotBeFound("city.in=" + UPDATED_CITY);
    }

    @Test
    @Transactional
    public void getAllAddressesByCityIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where city is not null
        defaultAddressShouldBeFound("city.specified=true");

        // Get all the addressList where city is null
        defaultAddressShouldNotBeFound("city.specified=false");
    }

    @Test
    @Transactional
    public void getAllAddressesByStateIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where state equals to DEFAULT_STATE
        defaultAddressShouldBeFound("state.equals=" + DEFAULT_STATE);

        // Get all the addressList where state equals to UPDATED_STATE
        defaultAddressShouldNotBeFound("state.equals=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllAddressesByStateIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where state in DEFAULT_STATE or UPDATED_STATE
        defaultAddressShouldBeFound("state.in=" + DEFAULT_STATE + "," + UPDATED_STATE);

        // Get all the addressList where state equals to UPDATED_STATE
        defaultAddressShouldNotBeFound("state.in=" + UPDATED_STATE);
    }

    @Test
    @Transactional
    public void getAllAddressesByStateIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where state is not null
        defaultAddressShouldBeFound("state.specified=true");

        // Get all the addressList where state is null
        defaultAddressShouldNotBeFound("state.specified=false");
    }

    @Test
    @Transactional
    public void getAllAddressesByCountryIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where country equals to DEFAULT_COUNTRY
        defaultAddressShouldBeFound("country.equals=" + DEFAULT_COUNTRY);

        // Get all the addressList where country equals to UPDATED_COUNTRY
        defaultAddressShouldNotBeFound("country.equals=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllAddressesByCountryIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where country in DEFAULT_COUNTRY or UPDATED_COUNTRY
        defaultAddressShouldBeFound("country.in=" + DEFAULT_COUNTRY + "," + UPDATED_COUNTRY);

        // Get all the addressList where country equals to UPDATED_COUNTRY
        defaultAddressShouldNotBeFound("country.in=" + UPDATED_COUNTRY);
    }

    @Test
    @Transactional
    public void getAllAddressesByCountryIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where country is not null
        defaultAddressShouldBeFound("country.specified=true");

        // Get all the addressList where country is null
        defaultAddressShouldNotBeFound("country.specified=false");
    }

    @Test
    @Transactional
    public void getAllAddressesByZipCodeIsEqualToSomething() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where zipCode equals to DEFAULT_ZIP_CODE
        defaultAddressShouldBeFound("zipCode.equals=" + DEFAULT_ZIP_CODE);

        // Get all the addressList where zipCode equals to UPDATED_ZIP_CODE
        defaultAddressShouldNotBeFound("zipCode.equals=" + UPDATED_ZIP_CODE);
    }

    @Test
    @Transactional
    public void getAllAddressesByZipCodeIsInShouldWork() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where zipCode in DEFAULT_ZIP_CODE or UPDATED_ZIP_CODE
        defaultAddressShouldBeFound("zipCode.in=" + DEFAULT_ZIP_CODE + "," + UPDATED_ZIP_CODE);

        // Get all the addressList where zipCode equals to UPDATED_ZIP_CODE
        defaultAddressShouldNotBeFound("zipCode.in=" + UPDATED_ZIP_CODE);
    }

    @Test
    @Transactional
    public void getAllAddressesByZipCodeIsNullOrNotNull() throws Exception {
        // Initialize the database
        addressRepository.saveAndFlush(address);

        // Get all the addressList where zipCode is not null
        defaultAddressShouldBeFound("zipCode.specified=true");

        // Get all the addressList where zipCode is null
        defaultAddressShouldNotBeFound("zipCode.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultAddressShouldBeFound(String filter) throws Exception {
        restAddressMockMvc.perform(get("/api/addresses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(address.getId().intValue())))
            .andExpect(jsonPath("$.[*].addressLine").value(hasItem(DEFAULT_ADDRESS_LINE.toString())))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2.toString())))
            .andExpect(jsonPath("$.[*].neighborhood").value(hasItem(DEFAULT_NEIGHBORHOOD.toString())))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY.toString())))
            .andExpect(jsonPath("$.[*].state").value(hasItem(DEFAULT_STATE.toString())))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY.toString())))
            .andExpect(jsonPath("$.[*].zipCode").value(hasItem(DEFAULT_ZIP_CODE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultAddressShouldNotBeFound(String filter) throws Exception {
        restAddressMockMvc.perform(get("/api/addresses?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingAddress() throws Exception {
        // Get the address
        restAddressMockMvc.perform(get("/api/addresses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAddress() throws Exception {
        // Initialize the database
        addressService.save(address);

        int databaseSizeBeforeUpdate = addressRepository.findAll().size();

        // Update the address
        Address updatedAddress = addressRepository.findOne(address.getId());
        // Disconnect from session so that the updates on updatedAddress are not directly saved in db
        em.detach(updatedAddress);
        updatedAddress
            .addressLine(UPDATED_ADDRESS_LINE)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .neighborhood(UPDATED_NEIGHBORHOOD)
            .city(UPDATED_CITY)
            .state(UPDATED_STATE)
            .country(UPDATED_COUNTRY)
            .zipCode(UPDATED_ZIP_CODE);

        restAddressMockMvc.perform(put("/api/addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAddress)))
            .andExpect(status().isOk());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate);
        Address testAddress = addressList.get(addressList.size() - 1);
        assertThat(testAddress.getAddressLine()).isEqualTo(UPDATED_ADDRESS_LINE);
        assertThat(testAddress.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testAddress.getNeighborhood()).isEqualTo(UPDATED_NEIGHBORHOOD);
        assertThat(testAddress.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testAddress.getState()).isEqualTo(UPDATED_STATE);
        assertThat(testAddress.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testAddress.getZipCode()).isEqualTo(UPDATED_ZIP_CODE);
    }

    @Test
    @Transactional
    public void updateNonExistingAddress() throws Exception {
        int databaseSizeBeforeUpdate = addressRepository.findAll().size();

        // Create the Address

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAddressMockMvc.perform(put("/api/addresses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(address)))
            .andExpect(status().isCreated());

        // Validate the Address in the database
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAddress() throws Exception {
        // Initialize the database
        addressService.save(address);

        int databaseSizeBeforeDelete = addressRepository.findAll().size();

        // Get the address
        restAddressMockMvc.perform(delete("/api/addresses/{id}", address.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Address> addressList = addressRepository.findAll();
        assertThat(addressList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Address.class);
        Address address1 = new Address();
        address1.setId(1L);
        Address address2 = new Address();
        address2.setId(address1.getId());
        assertThat(address1).isEqualTo(address2);
        address2.setId(2L);
        assertThat(address1).isNotEqualTo(address2);
        address1.setId(null);
        assertThat(address1).isNotEqualTo(address2);
    }
}

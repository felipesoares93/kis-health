package com.kis.health.service.dto;

import java.io.Serializable;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;






/**
 * Criteria class for the Address entity. This class is used in AddressResource to
 * receive all the possible filtering options from the Http GET request parameters.
 * For example the following could be a valid requests:
 * <code> /addresses?id.greaterThan=5&amp;attr1.contains=something&amp;attr2.specified=false</code>
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class AddressCriteria implements Serializable {
    private static final long serialVersionUID = 1L;


    private LongFilter id;

    private StringFilter addressLine;

    private StringFilter addressLine2;

    private StringFilter neighborhood;

    private StringFilter city;

    private StringFilter state;

    private StringFilter country;

    private StringFilter zipCode;

    public AddressCriteria() {
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(StringFilter addressLine) {
        this.addressLine = addressLine;
    }

    public StringFilter getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(StringFilter addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public StringFilter getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(StringFilter neighborhood) {
        this.neighborhood = neighborhood;
    }

    public StringFilter getCity() {
        return city;
    }

    public void setCity(StringFilter city) {
        this.city = city;
    }

    public StringFilter getState() {
        return state;
    }

    public void setState(StringFilter state) {
        this.state = state;
    }

    public StringFilter getCountry() {
        return country;
    }

    public void setCountry(StringFilter country) {
        this.country = country;
    }

    public StringFilter getZipCode() {
        return zipCode;
    }

    public void setZipCode(StringFilter zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return "AddressCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (addressLine != null ? "addressLine=" + addressLine + ", " : "") +
                (addressLine2 != null ? "addressLine2=" + addressLine2 + ", " : "") +
                (neighborhood != null ? "neighborhood=" + neighborhood + ", " : "") +
                (city != null ? "city=" + city + ", " : "") +
                (state != null ? "state=" + state + ", " : "") +
                (country != null ? "country=" + country + ", " : "") +
                (zipCode != null ? "zipCode=" + zipCode + ", " : "") +
            "}";
    }

}

<template>
  <div>
    <b-dropdown-item to="/trx-event">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxEvent')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-tournament">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxTournament')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-service">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstService')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-testimonial">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxTestimonial')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-region">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstRegion')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-country">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstCountry')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-province">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstProvince')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-city">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstCity')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-district">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstDistrict')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-sub-district">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstSubDistrict')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-postal-code">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstPostalCode')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/location">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.location')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-department">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstDepartment')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-task">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstTask')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-employee">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstEmployee')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-job">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstJob')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/job-history">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.jobHistory')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-product">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstProduct')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-category">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstCategory')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-brand">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstBrand')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-supplier">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstSupplier')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/stock">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.stock')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-order-stock">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxOrderStock')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-cart">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxCart')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-order">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxOrder')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-order-item">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxOrderItem')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-customer">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstCustomer')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-loyalty-program">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstLoyaltyProgram')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-delivery">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxDelivery')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/mst-driver">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.mstDriver')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-order-history">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxOrderHistory')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-product-history">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxProductHistory')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-discount">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxDiscount')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-coupon">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxCoupon')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-notification">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxNotification')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/trx-stock-alert">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.trxStockAlert')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

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
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

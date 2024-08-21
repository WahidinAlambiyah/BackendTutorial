<template>
  <div>
    <b-dropdown-item to="/region">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.region')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/country">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.country')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/location">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.location')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/department">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.department')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/task">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.task')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/employee">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.employee')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/job">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.job')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/job-history">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.jobHistory')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/province">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.province')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/city">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.city')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/district">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.district')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/sub-district">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.subDistrict')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/postal-code">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.postalCode')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

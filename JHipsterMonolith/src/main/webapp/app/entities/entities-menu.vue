<template>
  <div>
    <b-dropdown-item to="/event">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.event')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/tournament">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.tournament')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/service">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.service')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/testimonial">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.testimonial')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="postalCode">
        <h2 class="jh-entity-heading" data-cy="postalCodeDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.postalCode.detail.title')"></span> {{ postalCode.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.postalCode.code')"></span>
          </dt>
          <dd>
            <span>{{ postalCode.code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.postalCode.subDistrict')"></span>
          </dt>
          <dd>
            <div v-if="postalCode.subDistrict">
              <router-link :to="{ name: 'SubDistrictView', params: { subDistrictId: postalCode.subDistrict.id } }">{{
                postalCode.subDistrict.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="postalCode.id"
          :to="{ name: 'PostalCodeEdit', params: { postalCodeId: postalCode.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./postal-code-details.component.ts"></script>

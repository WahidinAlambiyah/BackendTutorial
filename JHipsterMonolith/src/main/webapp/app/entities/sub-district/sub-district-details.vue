<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="subDistrict">
        <h2 class="jh-entity-heading" data-cy="subDistrictDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.subDistrict.detail.title')"></span> {{ subDistrict.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.subDistrict.name')"></span>
          </dt>
          <dd>
            <span>{{ subDistrict.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.subDistrict.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ subDistrict.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.subDistrict.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ subDistrict.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.subDistrict.district')"></span>
          </dt>
          <dd>
            <div v-if="subDistrict.district">
              <router-link :to="{ name: 'DistrictView', params: { districtId: subDistrict.district.id } }">{{
                subDistrict.district.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="subDistrict.id"
          :to="{ name: 'SubDistrictEdit', params: { subDistrictId: subDistrict.id } }"
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

<script lang="ts" src="./sub-district-details.component.ts"></script>

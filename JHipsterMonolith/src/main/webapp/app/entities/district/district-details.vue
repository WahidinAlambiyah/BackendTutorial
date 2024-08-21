<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="district">
        <h2 class="jh-entity-heading" data-cy="districtDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.district.detail.title')"></span> {{ district.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.district.name')"></span>
          </dt>
          <dd>
            <span>{{ district.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.district.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ district.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.district.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ district.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.district.city')"></span>
          </dt>
          <dd>
            <div v-if="district.city">
              <router-link :to="{ name: 'CityView', params: { cityId: district.city.id } }">{{ district.city.name }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="district.id" :to="{ name: 'DistrictEdit', params: { districtId: district.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./district-details.component.ts"></script>

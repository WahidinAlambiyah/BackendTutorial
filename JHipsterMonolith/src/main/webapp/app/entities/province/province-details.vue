<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="province">
        <h2 class="jh-entity-heading" data-cy="provinceDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.province.detail.title')"></span> {{ province.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.province.name')"></span>
          </dt>
          <dd>
            <span>{{ province.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.province.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ province.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.province.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ province.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.province.country')"></span>
          </dt>
          <dd>
            <div v-if="province.country">
              <router-link :to="{ name: 'CountryView', params: { countryId: province.country.id } }">{{
                province.country.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="province.id" :to="{ name: 'ProvinceEdit', params: { provinceId: province.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./province-details.component.ts"></script>

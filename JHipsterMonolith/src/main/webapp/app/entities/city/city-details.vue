<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="city">
        <h2 class="jh-entity-heading" data-cy="cityDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.city.detail.title')"></span> {{ city.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.city.name')"></span>
          </dt>
          <dd>
            <span>{{ city.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.city.code')"></span>
          </dt>
          <dd>
            <span>{{ city.code }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.city.province')"></span>
          </dt>
          <dd>
            <div v-if="city.province">
              <router-link :to="{ name: 'ProvinceView', params: { provinceId: city.province.id } }">{{ city.province.name }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="city.id" :to="{ name: 'CityEdit', params: { cityId: city.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./city-details.component.ts"></script>

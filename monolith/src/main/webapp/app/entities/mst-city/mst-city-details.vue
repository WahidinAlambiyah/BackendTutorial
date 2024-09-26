<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstCity">
        <h2 class="jh-entity-heading" data-cy="mstCityDetailsHeading">
          <span v-text="t$('monolithApp.mstCity.detail.title')"></span> {{ mstCity.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstCity.name')"></span>
          </dt>
          <dd>
            <span>{{ mstCity.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCity.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ mstCity.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCity.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ mstCity.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCity.province')"></span>
          </dt>
          <dd>
            <div v-if="mstCity.province">
              <router-link :to="{ name: 'MstProvinceView', params: { mstProvinceId: mstCity.province.id } }">{{
                mstCity.province.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="mstCity.id" :to="{ name: 'MstCityEdit', params: { mstCityId: mstCity.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-city-details.component.ts"></script>

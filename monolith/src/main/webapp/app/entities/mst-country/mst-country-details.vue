<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstCountry">
        <h2 class="jh-entity-heading" data-cy="mstCountryDetailsHeading">
          <span v-text="t$('monolithApp.mstCountry.detail.title')"></span> {{ mstCountry.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstCountry.name')"></span>
          </dt>
          <dd>
            <span>{{ mstCountry.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCountry.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ mstCountry.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCountry.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ mstCountry.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstCountry.region')"></span>
          </dt>
          <dd>
            <div v-if="mstCountry.region">
              <router-link :to="{ name: 'MstRegionView', params: { mstRegionId: mstCountry.region.id } }">{{
                mstCountry.region.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstCountry.id"
          :to="{ name: 'MstCountryEdit', params: { mstCountryId: mstCountry.id } }"
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

<script lang="ts" src="./mst-country-details.component.ts"></script>

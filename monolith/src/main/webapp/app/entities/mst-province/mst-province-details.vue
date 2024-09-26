<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstProvince">
        <h2 class="jh-entity-heading" data-cy="mstProvinceDetailsHeading">
          <span v-text="t$('monolithApp.mstProvince.detail.title')"></span> {{ mstProvince.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstProvince.name')"></span>
          </dt>
          <dd>
            <span>{{ mstProvince.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProvince.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ mstProvince.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProvince.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ mstProvince.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstProvince.country')"></span>
          </dt>
          <dd>
            <div v-if="mstProvince.country">
              <router-link :to="{ name: 'MstCountryView', params: { mstCountryId: mstProvince.country.id } }">{{
                mstProvince.country.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstProvince.id"
          :to="{ name: 'MstProvinceEdit', params: { mstProvinceId: mstProvince.id } }"
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

<script lang="ts" src="./mst-province-details.component.ts"></script>

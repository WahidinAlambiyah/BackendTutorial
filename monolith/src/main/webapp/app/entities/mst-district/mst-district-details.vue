<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstDistrict">
        <h2 class="jh-entity-heading" data-cy="mstDistrictDetailsHeading">
          <span v-text="t$('monolithApp.mstDistrict.detail.title')"></span> {{ mstDistrict.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstDistrict.name')"></span>
          </dt>
          <dd>
            <span>{{ mstDistrict.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstDistrict.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ mstDistrict.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstDistrict.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ mstDistrict.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstDistrict.city')"></span>
          </dt>
          <dd>
            <div v-if="mstDistrict.city">
              <router-link :to="{ name: 'MstCityView', params: { mstCityId: mstDistrict.city.id } }">{{
                mstDistrict.city.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstDistrict.id"
          :to="{ name: 'MstDistrictEdit', params: { mstDistrictId: mstDistrict.id } }"
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

<script lang="ts" src="./mst-district-details.component.ts"></script>

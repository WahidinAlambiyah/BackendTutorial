<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstSubDistrict">
        <h2 class="jh-entity-heading" data-cy="mstSubDistrictDetailsHeading">
          <span v-text="t$('monolithApp.mstSubDistrict.detail.title')"></span> {{ mstSubDistrict.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstSubDistrict.name')"></span>
          </dt>
          <dd>
            <span>{{ mstSubDistrict.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstSubDistrict.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ mstSubDistrict.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstSubDistrict.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ mstSubDistrict.isoAlpha2Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstSubDistrict.district')"></span>
          </dt>
          <dd>
            <div v-if="mstSubDistrict.district">
              <router-link :to="{ name: 'MstDistrictView', params: { mstDistrictId: mstSubDistrict.district.id } }">{{
                mstSubDistrict.district.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstSubDistrict.id"
          :to="{ name: 'MstSubDistrictEdit', params: { mstSubDistrictId: mstSubDistrict.id } }"
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

<script lang="ts" src="./mst-sub-district-details.component.ts"></script>

<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstPostalCode">
        <h2 class="jh-entity-heading" data-cy="mstPostalCodeDetailsHeading">
          <span v-text="t$('monolithApp.mstPostalCode.detail.title')"></span> {{ mstPostalCode.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstPostalCode.code')"></span>
          </dt>
          <dd>
            <span>{{ mstPostalCode.code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstPostalCode.subDistrict')"></span>
          </dt>
          <dd>
            <div v-if="mstPostalCode.subDistrict">
              <router-link :to="{ name: 'MstSubDistrictView', params: { mstSubDistrictId: mstPostalCode.subDistrict.id } }">{{
                mstPostalCode.subDistrict.name
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstPostalCode.id"
          :to="{ name: 'MstPostalCodeEdit', params: { mstPostalCodeId: mstPostalCode.id } }"
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

<script lang="ts" src="./mst-postal-code-details.component.ts"></script>

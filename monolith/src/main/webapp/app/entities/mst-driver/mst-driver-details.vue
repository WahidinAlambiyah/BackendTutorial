<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstDriver">
        <h2 class="jh-entity-heading" data-cy="mstDriverDetailsHeading">
          <span v-text="t$('monolithApp.mstDriver.detail.title')"></span> {{ mstDriver.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstDriver.name')"></span>
          </dt>
          <dd>
            <span>{{ mstDriver.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstDriver.contactNumber')"></span>
          </dt>
          <dd>
            <span>{{ mstDriver.contactNumber }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstDriver.vehicleDetails')"></span>
          </dt>
          <dd>
            <span>{{ mstDriver.vehicleDetails }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstDriver.id"
          :to="{ name: 'MstDriverEdit', params: { mstDriverId: mstDriver.id } }"
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

<script lang="ts" src="./mst-driver-details.component.ts"></script>

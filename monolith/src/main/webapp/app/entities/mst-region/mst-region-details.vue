<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstRegion">
        <h2 class="jh-entity-heading" data-cy="mstRegionDetailsHeading">
          <span v-text="t$('monolithApp.mstRegion.detail.title')"></span> {{ mstRegion.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstRegion.name')"></span>
          </dt>
          <dd>
            <span>{{ mstRegion.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstRegion.unm49Code')"></span>
          </dt>
          <dd>
            <span>{{ mstRegion.unm49Code }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstRegion.isoAlpha2Code')"></span>
          </dt>
          <dd>
            <span>{{ mstRegion.isoAlpha2Code }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstRegion.id"
          :to="{ name: 'MstRegionEdit', params: { mstRegionId: mstRegion.id } }"
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

<script lang="ts" src="./mst-region-details.component.ts"></script>

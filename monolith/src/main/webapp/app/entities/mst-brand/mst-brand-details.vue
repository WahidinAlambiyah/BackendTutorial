<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstBrand">
        <h2 class="jh-entity-heading" data-cy="mstBrandDetailsHeading">
          <span v-text="t$('monolithApp.mstBrand.detail.title')"></span> {{ mstBrand.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstBrand.name')"></span>
          </dt>
          <dd>
            <span>{{ mstBrand.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstBrand.logo')"></span>
          </dt>
          <dd>
            <span>{{ mstBrand.logo }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstBrand.description')"></span>
          </dt>
          <dd>
            <span>{{ mstBrand.description }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="mstBrand.id" :to="{ name: 'MstBrandEdit', params: { mstBrandId: mstBrand.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./mst-brand-details.component.ts"></script>

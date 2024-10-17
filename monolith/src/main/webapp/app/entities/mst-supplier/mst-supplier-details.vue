<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstSupplier">
        <h2 class="jh-entity-heading" data-cy="mstSupplierDetailsHeading">
          <span v-text="t$('monolithApp.mstSupplier.detail.title')"></span> {{ mstSupplier.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstSupplier.name')"></span>
          </dt>
          <dd>
            <span>{{ mstSupplier.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstSupplier.contactInfo')"></span>
          </dt>
          <dd>
            <span>{{ mstSupplier.contactInfo }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstSupplier.address')"></span>
          </dt>
          <dd>
            <span>{{ mstSupplier.address }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstSupplier.rating')"></span>
          </dt>
          <dd>
            <span>{{ mstSupplier.rating }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstSupplier.id"
          :to="{ name: 'MstSupplierEdit', params: { mstSupplierId: mstSupplier.id } }"
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

<script lang="ts" src="./mst-supplier-details.component.ts"></script>

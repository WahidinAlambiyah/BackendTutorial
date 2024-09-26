<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="mstService">
        <h2 class="jh-entity-heading" data-cy="mstServiceDetailsHeading">
          <span v-text="t$('monolithApp.mstService.detail.title')"></span> {{ mstService.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('monolithApp.mstService.name')"></span>
          </dt>
          <dd>
            <span>{{ mstService.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstService.description')"></span>
          </dt>
          <dd>
            <span>{{ mstService.description }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstService.price')"></span>
          </dt>
          <dd>
            <span>{{ mstService.price }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstService.durationInHours')"></span>
          </dt>
          <dd>
            <span>{{ mstService.durationInHours }}</span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstService.serviceType')"></span>
          </dt>
          <dd>
            <span v-text="t$('monolithApp.ServiceType.' + mstService.serviceType)"></span>
          </dd>
          <dt>
            <span v-text="t$('monolithApp.mstService.testimonial')"></span>
          </dt>
          <dd>
            <div v-if="mstService.testimonial">
              <router-link :to="{ name: 'TrxTestimonialView', params: { trxTestimonialId: mstService.testimonial.id } }">{{
                mstService.testimonial.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="mstService.id"
          :to="{ name: 'MstServiceEdit', params: { mstServiceId: mstService.id } }"
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

<script lang="ts" src="./mst-service-details.component.ts"></script>

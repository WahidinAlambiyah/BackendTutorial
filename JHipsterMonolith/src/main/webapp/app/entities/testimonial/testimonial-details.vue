<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="testimonial">
        <h2 class="jh-entity-heading" data-cy="testimonialDetailsHeading">
          <span v-text="t$('jHipsterMonolithApp.testimonial.detail.title')"></span> {{ testimonial.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('jHipsterMonolithApp.testimonial.name')"></span>
          </dt>
          <dd>
            <span>{{ testimonial.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.testimonial.feedback')"></span>
          </dt>
          <dd>
            <span>{{ testimonial.feedback }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.testimonial.rating')"></span>
          </dt>
          <dd>
            <span>{{ testimonial.rating }}</span>
          </dd>
          <dt>
            <span v-text="t$('jHipsterMonolithApp.testimonial.date')"></span>
          </dt>
          <dd>
            <span v-if="testimonial.date">{{ formatDateLong(testimonial.date) }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link
          v-if="testimonial.id"
          :to="{ name: 'TestimonialEdit', params: { testimonialId: testimonial.id } }"
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

<script lang="ts" src="./testimonial-details.component.ts"></script>
